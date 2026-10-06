package com.revHub.integration.vehiclesdb.service;

import com.revHub.integration.vehiclesdb.client.VehiclesDbClient;
import com.revHub.integration.vehiclesdb.dto.MakeDto;
import com.revHub.integration.vehiclesdb.dto.ModelDto;
import com.revHub.integration.vehiclesdb.dto.VehiclesDbResponse;
import com.revHub.integration.vehiclesdb.entity.VehicleMake;
import com.revHub.integration.vehiclesdb.entity.VehicleModel;
import com.revHub.integration.vehiclesdb.repository.VehicleMakeRepository;
import com.revHub.integration.vehiclesdb.repository.VehicleModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleCatalogSyncService {

    private final VehiclesDbClient client;
    private final VehicleMakeRepository makeRepository;
    private final VehicleModelRepository modelRepository;

    @Transactional
    public void sync() {

        VehiclesDbResponse response = client.download();

        if (response == null || response.makes() == null) {
            return;
        }

        for (MakeDto makeDto : response.makes()) {

            if (makeDto == null || makeDto.name() == null) {
                continue;
            }

            String normalizedMake =
                    VehicleNameNormalizer.normalize(makeDto.name());

            /*
             * Existing Make:
             *     DO NOTHING
             *
             * New Make:
             *     INSERT
             */
            VehicleMake make =
                    makeRepository
                            .findByNormalizedName(normalizedMake)
                            .orElseGet(() -> {

                                VehicleMake newMake = new VehicleMake();

                                newMake.setName(makeDto.name().trim());
                                newMake.setNormalizedName(normalizedMake);
                                newMake.setSlug(makeDto.slug());
                                newMake.setActive(true);
                                newMake.setSource("VEHICLES DB");

                                return makeRepository.save(newMake);
                            });

            /*
             * Import Models
             */
            if (makeDto.models() == null) {
                continue;
            }

            for (ModelDto modelDto : makeDto.models()) {

                if (modelDto == null || modelDto.name() == null) {
                    continue;
                }

                String normalizedModel =
                        VehicleNameNormalizer.normalize(modelDto.name());

                /*
                 * Existing Model:
                 *     DO NOTHING
                 *
                 * New Model:
                 *     INSERT
                 */
                if (modelRepository.existsByMakeIdAndNormalizedName(
                        make.getId(),
                        normalizedModel
                )) {
                    continue;
                }

                VehicleModel newModel = new VehicleModel();

                newModel.setMake(make);
                newModel.setName(modelDto.name().trim());
                newModel.setNormalizedName(normalizedModel);
                newModel.setSlug(modelDto.slug());
                newModel.setKind(modelDto.kind());
                newModel.setActive(true);
                newModel.setSource("VEHICLESDB");

                modelRepository.save(newModel);
            }
        }
    }
}
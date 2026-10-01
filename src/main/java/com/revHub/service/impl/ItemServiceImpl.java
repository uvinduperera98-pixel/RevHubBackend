package com.revHub.service.impl;

import com.revHub.dto.request.ItemModifyRequestDTO;
import com.revHub.dto.request.ItemSaveRequestDTO;
import com.revHub.dto.response.InvoiceItemsResponseDTO;
import com.revHub.dto.response.ItemIdNameResponseDto;
import com.revHub.dto.response.ItemResponseDto;
import com.revHub.dto.response.ItemTableViewResponseProjection;
import com.revHub.entity.Item;
import com.revHub.entity.ItemHistory;
import com.revHub.entity.LaborActivity;
import com.revHub.exception.DuplicateException;
import com.revHub.exception.NotFoundException;
import com.revHub.repository.ItemHistoryRepository;
import com.revHub.repository.ItemRepository;
import com.revHub.repository.LaborActivityRepository;
import com.revHub.service.ItemService;
import com.revHub.util.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor // Automatically generates a constructor for final fields (Constructor Injection)
@Transactional(readOnly = true) // Default transaction configuration for the service layer
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final LaborActivityRepository laborActivityRepository;
    private final ItemHistoryRepository itemHistoryRepository;
    private final ModelMapper modelMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public void saveItemDetails(ItemSaveRequestDTO itemSaveRequestDto) {

        Item item = modelMapper.map(itemSaveRequestDto, Item.class);

        // Check duplicate item name
        if (itemRepository.existsByItemName(item.getItemName())) {
            throw new DuplicateException("Already added Item");
        }

        // Handle Many-to-Many relationship
        if (itemSaveRequestDto.getLaborActivitiesSelected() != null && !itemSaveRequestDto.getLaborActivitiesSelected().isEmpty()) {
            List<LaborActivity> activities = laborActivityRepository.findAllById(itemSaveRequestDto.getLaborActivitiesSelected());
            item.setLaborActivities(activities);
        }

        itemRepository.save(item);
    }

    @Override
    public List<ItemResponseDto> fetchAllItemDetails() {
        List<Item> items = itemRepository.findAll();

        if (items.isEmpty()) {
            throw new NotFoundException("No Data");
        }

        return itemMapper.listItemsToDto(items);
    }

    @Override
    public List<ItemTableViewResponseProjection> fetchAllItems() {
        // Stream directly from JDBC to projection proxies
        List<ItemTableViewResponseProjection> items = itemRepository.findAllItems();

        if (items.isEmpty()) {
            throw new NotFoundException("No inventory data found.");
        }

        return items;
    }

    @Override
    public ItemTableViewResponseProjection getItemById(Long itemId) {
        return itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(ItemModifyRequestDTO itemModifyRequestDTO) {

        // 1. Check if the Item exists
        Item item = itemRepository.findById(itemModifyRequestDTO.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        // 2. Check for duplicate name, ignoring the current Item ID
        boolean nameExists = itemRepository.existsByItemNameAndItemIdNot(
                itemModifyRequestDTO.getItemName(),
                itemModifyRequestDTO.getItemId()
        );

        if (nameExists) {
            throw new DuplicateException("Item name already exists");
        }

        // 3. Save previous state to Item History before modifying
        ItemHistory history = new ItemHistory();
        history.setItemId(item.getItemId());
        history.setItemName(item.getItemName());
        history.setMeasuringUnitType(item.getMeasuringUnitType());
        history.setBalanceQty(item.getBalanceQty());
        history.setSupplierPrice(item.getSupplierPrice());
        history.setSellingPrice(item.getSellingPrice());
        history.setActionType("UPDATE");

        itemHistoryRepository.save(history);

        // 4. Fetch LaborActivity entities
        List<LaborActivity> laborActivities = laborActivityRepository.findAllById(
                itemModifyRequestDTO.getLaborActivitiesSelected()
        );

        // 5. Update Item fields
        item.setLaborActivities(laborActivities);
        item.setItemName(itemModifyRequestDTO.getItemName());
        item.setBalanceQty(itemModifyRequestDTO.getBalanceQty());
        item.setSellingPrice(itemModifyRequestDTO.getSellingPrice());
        item.setSupplierPrice(itemModifyRequestDTO.getSupplierPrice());
        item.setMeasuringUnitType(itemModifyRequestDTO.getMeasuringUnitType());

        // 6. Save updated Item
        itemRepository.save(item);
    }

    @Override
    public List<ItemIdNameResponseDto> getAllItemNameList() {
        List<Object[]> itemIdsAndNamesList = itemRepository.findAllItemIdsAndNames();

        return itemIdsAndNamesList.stream()
                .map(row -> new ItemIdNameResponseDto((Long) row[0], (String) row[1]))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ItemTableViewResponseProjection> getAllItemPaginated(Pageable pageable, Long itemId) {

        if (pageable.getSort().stream().anyMatch(order -> order.getProperty().equalsIgnoreCase("string"))) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdDate").descending()
            );
        }

        return itemRepository.findAllItemProjectedBy(itemId, pageable);
    }

    @Override
    public List<InvoiceItemsResponseDTO> fetchAllInvoiceItems() {
        List<InvoiceItemsResponseDTO> items = itemRepository.fetchAllInvoiceItems();

        if (items.isEmpty()) {
            throw new NotFoundException("No item data found.");
        }

        return items;
    }
}

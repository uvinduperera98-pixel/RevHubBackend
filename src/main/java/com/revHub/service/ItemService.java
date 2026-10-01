package com.revHub.service;

import com.revHub.dto.request.ItemModifyRequestDTO;
import com.revHub.dto.request.ItemSaveRequestDTO;
import com.revHub.dto.response.InvoiceItemsResponseDTO;
import com.revHub.dto.response.ItemIdNameResponseDto;
import com.revHub.dto.response.ItemResponseDto;
import com.revHub.dto.response.ItemTableViewResponseProjection;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ItemService {

    void saveItemDetails(ItemSaveRequestDTO itemSaveRequestDto);

    List<ItemResponseDto> fetchAllItemDetails();

    List<ItemTableViewResponseProjection> fetchAllItems();

    ItemTableViewResponseProjection getItemById(Long itemId);

    void updateItem(ItemModifyRequestDTO itemModifyRequestDTO);

    List<ItemIdNameResponseDto> getAllItemNameList();

    Page<ItemTableViewResponseProjection> getAllItemPaginated(Pageable pageable, Long itemId);

    List<InvoiceItemsResponseDTO> fetchAllInvoiceItems();
}

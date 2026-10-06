package com.revHub.controller;

import com.revHub.dto.request.ItemModifyRequestDTO;
import com.revHub.dto.request.ItemSaveRequestDTO;
import com.revHub.dto.response.InvoiceItemsResponseDTO;
import com.revHub.dto.response.ItemIdNameResponseDto;
import com.revHub.dto.response.ItemResponseDto;
import com.revHub.dto.response.ItemTableViewResponseProjection;
import com.revHub.service.ItemService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    @PostMapping("/save")
    public ResponseEntity<StandardResponse> saveItemDetails(@RequestBody ItemSaveRequestDTO itemSaveRequestDto) {
        itemService.saveItemDetails(itemSaveRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StandardResponse(201,
                "Item saved successfully", null));
    }

    @GetMapping("/fetch-all")
    public ResponseEntity<StandardResponse> fetchAllItemDetails() {
        List<ItemResponseDto> itemDtos = itemService.fetchAllItemDetails();

        return ResponseEntity.ok(new StandardResponse(200, "Success", itemDtos));
    }

    @GetMapping(value = "/fetch-all-items")
    public ResponseEntity<StandardResponse> fetchAllItems() {
        List<ItemTableViewResponseProjection> items = itemService.fetchAllItems();

        return ResponseEntity.ok(new StandardResponse(200, "Success", items));
    }

    @PostMapping("/get-all-items")
    public ResponseEntity<StandardResponse> getAllItem(
            @RequestParam(required = false) Long itemId,
            @PageableDefault(page = 0, size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ItemTableViewResponseProjection> itemPage = itemService.getAllItemPaginated(pageable, itemId);

        return ResponseEntity.ok(new StandardResponse(200, "Item retrieved successfully", itemPage));
    }

    @GetMapping("/get-item-by-itemId/{itemId}")
    public ResponseEntity<StandardResponse> getItemById(@PathVariable Long itemId) {
        ItemTableViewResponseProjection item = itemService.getItemById(itemId);

        return ResponseEntity.ok(new StandardResponse(200, "Success", item));
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateJobCard(@RequestBody ItemModifyRequestDTO itemModifyRequestDTO) {
        itemService.updateItem(itemModifyRequestDTO);

        return ResponseEntity.ok(new StandardResponse(200, "Item updated successfully", null));
    }

    @GetMapping("/get-all-item-names")
    public ResponseEntity<StandardResponse> getAllItemNameList() {
        List<ItemIdNameResponseDto> itemList = itemService.getAllItemNameList();

        return ResponseEntity.ok(new StandardResponse(200, "Success", itemList));
    }

    @GetMapping(value = "/fetch-all-invoice-items")
    public ResponseEntity<StandardResponse> fetchAllInvoiceItems() {
        List<InvoiceItemsResponseDTO> items = itemService.fetchAllInvoiceItems();

        return ResponseEntity.ok(new StandardResponse(200, "Success", items));
    }
}

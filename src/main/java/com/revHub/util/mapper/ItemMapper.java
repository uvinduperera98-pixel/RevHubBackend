package com.revHub.util.mapper;

import com.revHub.dto.response.ItemResponseDto;
import com.revHub.entity.Item;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;
@Mapper(componentModel = "spring")
public interface ItemMapper {

    // Mapping method for single item
    ItemResponseDto map(Item item);

    // Mapping method for list of items
    List<ItemResponseDto> listDtoToPage(Page<Item> items);

    List<ItemResponseDto> listItemsToDto(List<Item> items);
}


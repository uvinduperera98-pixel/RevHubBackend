package com.revHub.repository;

import com.revHub.dto.response.InvoiceItemResponseDTO;
import com.revHub.dto.response.InvoiceItemsResponseDTO;
import com.revHub.dto.response.ItemTableViewResponseProjection;
import com.revHub.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByItemName(String itemName);

    Page<Item> findByItemNameEquals(String itemName, Pageable pageable);

    long countByItemNameEquals(String itemName);

    // Native SQL query mapping directly to the projection proxy
    @Query(value = "SELECT item_id AS itemId, " +
            "item_name AS itemName, " +
            "balance_qty AS balanceQty, " +
            "supplier_price AS supplierPrice, " +
            "selling_price AS sellingPrice, " +
            "measuring_unit_type AS measuringUnitType " +
            "FROM item",
            nativeQuery = true)
    List<ItemTableViewResponseProjection> findAllItems();

    @Query(value = "SELECT item_id AS itemId, " +
            "item_name AS itemName, " +
            "balance_qty AS balanceQty, " +
            "created_date AS dateAdded, " +
            "last_modified_date AS lastModifyDate, " +
            "last_modified_user AS modifyUser, " +
            "supplier_price AS supplierPrice, " +
            "selling_price AS sellingPrice, " +
            "measuring_unit_type AS measuringUnitType " +
            "FROM item",
            countQuery = "SELECT count(*) FROM item",
            nativeQuery = true)
    Page<ItemTableViewResponseProjection> findAllItemsProjectedBy(Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.itemId = :itemId")
    Optional<ItemTableViewResponseProjection> findItemByItemId(@Param("itemId") Long itemId);

    @Query("SELECT i.id, i.itemName FROM Item i WHERE i.itemName IS NOT NULL AND TRIM(i.itemName) <> '' AND i.itemName <> 'N/A'")
    List<Object[]> findAllItemIdsAndNames();

    @Query("SELECT i.itemId as itemId, i.itemName as itemName, i.balanceQty as balanceQty, " +
            "i.createdDate as createdDate, i.lastModifiedDate as lastModifiedDate, i.lastModifiedUser as lastModifiedUser, " +
            "i.supplierPrice as supplierPrice, i.sellingPrice as sellingPrice, i.measuringUnitType as measuringUnitType " +
            "FROM Item i WHERE (:itemId IS NULL OR i.itemId = :itemId)")
    Page<ItemTableViewResponseProjection> findAllItemProjectedBy(
            @Param("itemId") Long itemId,
            Pageable pageable
    );

    boolean existsByItemName(String itemName);

    boolean existsByItemNameAndItemIdNot(String itemName, Long itemId);

    @Query("SELECT new com.revHub.dto.response.InvoiceItemsResponseDTO(i.itemId, i.itemName, i.measuringUnitType, i.sellingPrice) FROM Item i")
    List<InvoiceItemsResponseDTO> fetchAllInvoiceItems();
}

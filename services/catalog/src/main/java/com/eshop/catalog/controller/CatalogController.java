package com.eshop.catalog.controller;

import com.eshop.catalog.model.Brand;
import com.eshop.catalog.model.CatalogItem;
import com.eshop.catalog.model.Category;
import com.eshop.catalog.services.CatalogService;
import com.eshop.shared.rest.error.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Handle requests for catalog service.
 */
@RequestMapping("catalog")
@RestController
@RequiredArgsConstructor
public class CatalogController {
    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;

    /**
     * Returns catalog items for given item ids.
     *
     * @param ids catalog item ids
     * @return catalog items
     */
    @RequestMapping("items/withids/{ids}")
    public Iterable<CatalogItem> catalogItemsByIds(@PathVariable String ids) {
        if (isEmpty(ids)) {
            throw new BadRequestException("Invalid ids value");
        }

        final var itemIds = Arrays.
                stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return catalogService.getItemsByIds(itemIds);
    }

    /**
     * Returns catalog items that belong to given brand and type in the given page.
     *
     * @param pageSize  number of items
     * @param pageIndex page
     * @param brandId   item brand
     * @param typeId    item type
     * @return catalog items
     */
    @RequestMapping("items")
    public Page<CatalogItem> catalogItems(
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) Integer pageIndex,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long typeId
    ) {
        logger.info("Find catalog items - page size: {}, page index: {}, brand: {}, type: {}", pageSize, pageIndex, brandId, typeId);
        final var brand = findBrand(brandId);
        final var type = findCategory(typeId);

        return catalogService.getItems(brand, type, PageRequest.of(pageIndex, pageSize));
    }

    /**
     * Returns catalog item by given id.
     *
     * @param id item id
     * @return catalog item
     */
    @RequestMapping("items/{id}")
    public ResponseEntity<CatalogItem> catalogItem(@PathVariable Long id) {
        logger.info("Find catalog item: {}", id);
        return ResponseEntity.of(catalogService.getItemById(id));
    }

    /**
     * Returns catalog items by given name in give page.
     *
     * @param pageSize  number of items to be returned
     * @param pageIndex page
     * @param name      name of item
     * @return catalog items
     */
    @RequestMapping("items/withname/{name}")
    public Page<CatalogItem> catalogItems(
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "0", required = false) Integer pageIndex,
            @PathVariable String name
    ) {
        if (isEmpty(name)) {
            throw new BadRequestException("The name must be at least one character long");
        }
        return catalogService.getItems(name, PageRequest.of(pageIndex, pageSize));
    }

    /**
     * Returns available types of items.
     *
     * @return catalog types
     */
    @RequestMapping("catalogtypes")
    public Iterable<Category> findAllCategories() {
        logger.info("Find all catalog types");
        return catalogService.getAllCategories();
    }

    /**
     * Returns available brands.
     *
     * @return catalog brands
     */
    @RequestMapping("catalogbrands")
    public Iterable<Brand> findAllBrands() {
        logger.info("Find all catalog brands");
        return catalogService.getAllBrands();
    }

    /**
     * Updates given catalog item.
     *
     * @param productToUpdate item to be updated
     */
    @RequestMapping(method = RequestMethod.PUT, path = "items")
    @Transactional
    public void updateProduct(@RequestBody @Valid CatalogItem productToUpdate) {
        logger.info("Update product: {}", productToUpdate.getId());
        catalogService.updateItem(productToUpdate);
    }

    @RequestMapping(method = RequestMethod.POST, path = "items")
    public void createProduct(@RequestBody @Valid CatalogItem product) {
        logger.info("Add product: {}", product.getId());
        catalogService.addItem(product);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "items/{id}")
    public void deleteProduct(@PathVariable Long id) {
        logger.info("Delete product: {}", id);
        catalogService.deleteItem(id);
    }

    private Brand findBrand(Long id) {
        return nonNull(id)
                ? catalogService.getBrandById(id)
                .orElseThrow(() -> new BadRequestException("Catalog item brand with id: %d does not exist".formatted(id)))
                : null;
    }

    private Category findCategory(Long id) {
        return nonNull(id)
                ? catalogService.getCategoryById(id)
                .orElseThrow(() -> new BadRequestException("Catalog item category with id: %d does not exist".formatted(id)))
                : null;
    }
}

package com.gw.pos.api.api;

import static com.gw.pos.api.api.ApiDtos.*;

import com.gw.pos.api.domain.ProductEntity;
import com.gw.pos.api.repo.ProductRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping
  public List<ProductDto> list(@RequestParam(name = "type", required = false) String type) {
    List<ProductEntity> products =
        (type == null || type.isBlank())
            ? productRepository.findAll()
            : productRepository.findByTypeOrderByPriceAsc(type);
    return products.stream()
        .map(p -> new ProductDto(p.getName(), p.getType(), p.getPrice(), p.getSoldVolume()))
        .toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDto create(@Valid @RequestBody CreateProductRequest req) {
    ProductEntity saved = productRepository.save(new ProductEntity(req.name(), req.type(), req.price(), 0));
    return new ProductDto(saved.getName(), saved.getType(), saved.getPrice(), saved.getSoldVolume());
  }

  @DeleteMapping("/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("name") String name) {
    productRepository.deleteById(name);
  }
}


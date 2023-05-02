package com.bullish.checkout.controller;

import com.bullish.checkout.BusinessException;
import com.bullish.checkout.Constants;
import com.bullish.checkout.InvalidDealException;
import com.bullish.checkout.ProductIdQueryParameterMissingException;
import com.bullish.checkout.domain.DealOperations;
import com.bullish.checkout.domain.DealType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


/*
    Administrative endpoints to add and remove deals
 */
@RestController
@RequestMapping(Constants.DEAL_BASE_PATH)
public class DealController {

    private DealOperations dealOperations;

    public DealController(DealOperations dealOperations) {
        this.dealOperations = dealOperations;
    }

    @GetMapping("/{id}")
    public DealResponseV1 getDeal(
            @PathVariable("id") String id
    ) {
        return DealResponseV1.from(dealOperations.getById(Long.valueOf(id)));

    }

    @GetMapping
    public List<DealResponseV1> getDeal(
            @RequestParam("productId") Optional<String> productId
    ) {
        if (productId.isEmpty()) {
            throw new ProductIdQueryParameterMissingException();
        }
        return dealOperations.getByProduct(Long.parseLong(productId.get()))
                .stream().map(DealResponseV1::from)
                .toList();

    }

    @PostMapping
    public DealResponseV1 addDeal(@RequestBody DealRequestV1 body) {
        if (body.getDealType() == null) {
            throw new InvalidDealException("Deal Type not provided");
        }

        if (DealType.valueOf(body.getDealType()).equals(DealType.FLAT_AMOUNT)) {
            if (body.getFlatDiscount() == 0) {
                throw new InvalidDealException("Flat Discount deal type must have a non-zero positive number provided as flatDiscount");

            }
            return DealResponseV1.from(dealOperations.createFlatDiscountDeal(
                    Long.parseLong(body.getProductId()),
                    BigDecimal.valueOf(body.getFlatDiscount()),
                    Long.valueOf(body.getMinimumQuantity()),
                    Long.valueOf(body.getMaximumQuantity())
            ));
        } else if ((DealType.valueOf(body.getDealType()).equals(DealType.PERCENTAGE))) {
            if (body.getDiscountPercentage() == 0) {
                throw new InvalidDealException("Percentage deal type must have a non-zero positive number provided as discountPercentage");

            }
            return DealResponseV1.from(dealOperations.createPercentageDiscountDeal(
                    Long.parseLong(body.getProductId()),
                    BigDecimal.valueOf(body.getDiscountPercentage()),
                    Long.valueOf(body.getMinimumQuantity()),
                    Long.valueOf(body.getMaximumQuantity())
            ));
        }
        throw new InvalidDealException("Unknown Deal Type");
    }

    @DeleteMapping("/{id}")
    public void deleteDeal(@PathVariable("id") String id) {

        dealOperations.deleteDeal(Long.parseLong(id));
        
    }
}

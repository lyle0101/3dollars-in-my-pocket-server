package com.depromeet.team5.controller;

import com.depromeet.team5.application.store.StoreApplicationService;
import com.depromeet.team5.domain.Location;
import com.depromeet.team5.domain.store.CategoryTypes;
import com.depromeet.team5.domain.store.Store;
import com.depromeet.team5.domain.store.StoresByCategoryAndRating;
import com.depromeet.team5.dto.CategoryDistanceDto;
import com.depromeet.team5.dto.CategoryReviewDto;
import com.depromeet.team5.dto.StoreCardDto;
import com.depromeet.team5.service.CategoryService;
import com.depromeet.team5.util.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(value = "Category")
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final StoreApplicationService storeApplicationService;

    @ApiOperation("거리순으로 특정 카테고리의 가게 정보를 가져옵니다. 인증이 필요한 요청입니다.")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header")
    @Auth
    @GetMapping("/distance")
    public ResponseEntity<CategoryDistanceDto> getStoresGroupByDistance(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Double mapLatitude,
            @RequestParam(required = false) Double mapLongitude,
            @RequestParam CategoryTypes category
    ) {
        Location mapLocation = mapLatitude != null && mapLongitude != null
                ? Location.of(mapLatitude, mapLongitude)
                : Location.of(latitude, longitude);
        return ResponseEntity.ok(
                storeApplicationService.getStoresByCategoryGroupByDistance(
                        category,
                        Location.of(latitude, longitude),
                        mapLocation
                )
        );
    }

    private List<StoreCardDto> toStoreCardDtoList(List<Store> stores, Double latitude, Double longitude) {
        return stores.stream()
                .map(store -> StoreCardDto.of(store, latitude, longitude))
                .collect(Collectors.toList());
    }

    @ApiOperation("리뷰순으로 특정 카테고리의 가게 정보를 가져옵니다. 인증이 필요한 요청입니다.")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header")
    @Auth
    @GetMapping("/review")
    public ResponseEntity<CategoryReviewDto> getReviewAll(@RequestParam Double latitude,
                                                          @RequestParam Double longitude,
                                                          @RequestParam CategoryTypes category) {
        return ResponseEntity.ok(
                toCategoryReviewDto(
                        categoryService.getStoresByCategoryAndRating(latitude, longitude, category),
                        latitude,
                        longitude
                )
        );
    }

    private CategoryReviewDto toCategoryReviewDto(
            StoresByCategoryAndRating storesByCategoryAndRating,
            Double latitude,
            Double longitude
    ) {
        CategoryReviewDto categoryReviewDto = new CategoryReviewDto();
        categoryReviewDto.setStoreList4(toStoreCardDtoList(storesByCategoryAndRating.getStoresRatingOver4(), latitude, longitude));
        categoryReviewDto.setStoreList3(toStoreCardDtoList(storesByCategoryAndRating.getStoresRatingOver3(), latitude, longitude));
        categoryReviewDto.setStoreList2(toStoreCardDtoList(storesByCategoryAndRating.getStoresRatingOver2(), latitude, longitude));
        categoryReviewDto.setStoreList1(toStoreCardDtoList(storesByCategoryAndRating.getStoresRatingOver1(), latitude, longitude));
        categoryReviewDto.setStoreList0(toStoreCardDtoList(storesByCategoryAndRating.getStoresRatingOver0(), latitude, longitude));
        return categoryReviewDto;
    }
}

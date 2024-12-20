package doit.shop.controller.product.repository;

import com.querydsl.core.types.Projections;
import doit.shop.controller.product.dto.ProductInfoResponse;
import doit.shop.exception.CustomException;
import doit.shop.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static doit.shop.controller.product.entity.QCategory.category;
import static doit.shop.controller.product.entity.QProduct.product;
import static doit.shop.controller.user.entity.QUserEntity.userEntity;

@RequiredArgsConstructor
@Component
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductInfoResponse> searchKeyword(Pageable pageable, String keyword) {

        List<ProductInfoResponse> products = queryFactory
                .select(Projections.constructor(
                    ProductInfoResponse.class,
                    product.productId,
                    product.name,
                    product.description,
                    product.price,
                    product.stock,
                    product.image,
                    product.createdAt,
                    product.modifiedAt,
                    product.category.categoryId,
                    category.categoryType,
                    userEntity.userId,
                    userEntity.nickname
                ))
                .from(product)
                .where(product.description.contains(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.modifiedAt.asc(), product.createdAt.asc())
                .fetch();

        Long total = queryFactory.select(product.count())
                .from(product)
                .leftJoin(category).on(product.category.categoryId.eq(category.categoryId))
                .leftJoin(userEntity).on(product.user.userId.eq(userEntity.userId))
                .where(product.description.contains(keyword))
                .fetchOne();

        if (products.isEmpty()) {
            throw new CustomException(ErrorCode.QUERY_NOT_FOUND);
        }

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<ProductInfoResponse> searchKeywordByCategoryId(Pageable pageable, String keyword, Long categoryId) {

        List<ProductInfoResponse> products = queryFactory.select(Projections.constructor(
                ProductInfoResponse.class,
                product.productId,
                product.name,
                product.description,
                product.price,
                product.stock,
                product.image,
                product.createdAt,
                product.modifiedAt,
                product.category.categoryId,
                category.categoryType,
                userEntity.userId,
                userEntity.nickname
            ))
            .from(product)
            .leftJoin(category).on(product.category.categoryId.eq(category.categoryId))
            .leftJoin(userEntity).on(product.user.userId.eq(userEntity.userId))
            .where(product.description.contains(keyword), product.category.categoryId.eq(categoryId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(product.modifiedAt.asc(), product.createdAt.asc())
            .fetch();

        Long total = queryFactory
            .select(product.count())
            .from(product)
            .leftJoin(category).on(product.category.categoryId.eq(category.categoryId))
            .leftJoin(userEntity).on(product.user.userId.eq(userEntity.userId))
            .where(product.description.contains(keyword), product.category.categoryId.eq(categoryId))
            .fetchOne();

        if (products.isEmpty()) {
            throw new CustomException(ErrorCode.QUERY_NOT_FOUND);
        }

        return new PageImpl<>(products, pageable, total);
    }
}

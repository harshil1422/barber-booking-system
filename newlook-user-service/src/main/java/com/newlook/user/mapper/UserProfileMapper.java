package com.newlook.user.mapper;

import com.newlook.user.dto.UserProfileResponse;
import com.newlook.user.entity.UserProfile;
import com.newlook.user.entity.UserRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(entity))")
    @Mapping(target = "hasShop", expression = "java(entity.hasShop())")
    @Mapping(target = "shopId", expression = "java(entity.hasShop() ? entity.getShopOwnerProfile().getShopId() : null)")
    UserProfileResponse toResponse(UserProfile entity);

    default List<com.newlook.user.enums.UserRole> mapRoles(UserProfile entity) {
        return entity.getRoles().stream()
                .map(UserRoleEntity::getRole)
                .collect(Collectors.toList());
    }
}

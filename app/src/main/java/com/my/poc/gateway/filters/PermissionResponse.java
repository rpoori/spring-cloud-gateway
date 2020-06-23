package com.my.poc.gateway.filters;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PermissionResponse {
    List<String> permissionsList;
}

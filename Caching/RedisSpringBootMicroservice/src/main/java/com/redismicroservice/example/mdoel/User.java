package com.redismicroservice.example.mdoel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("user_name")
    private String username;

    @JsonProperty("address")
    private String address;

    @JsonProperty("gender")
    private String gender;
}

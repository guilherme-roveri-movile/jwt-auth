package com.gbr.domains;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Getter
@Setter
@EqualsAndHashCode(of = {"uid"})
@Document(collection = "users")
public class User {
    @Id
    private String uid;
    private String password;
    @Indexed(unique = true)
    private String username;
    private Collection<String> authorities;
}

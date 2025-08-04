package cn.biq.mn.security;

import cn.biq.mn.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_token_blacklist")
@Getter
@Setter
public class TokenBlacklist extends BaseEntity {

    @Column(length = 512, nullable = false, unique = true)
    private String token;

}


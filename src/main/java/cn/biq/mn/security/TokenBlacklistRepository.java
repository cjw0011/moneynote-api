package cn.biq.mn.security;

import cn.biq.mn.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends BaseRepository<TokenBlacklist> {

    boolean existsByToken(String token);

}


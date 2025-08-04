package cn.biq.mn.security;

import cn.biq.mn.base.BaseRepository;
import cn.biq.mn.user.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshToken> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

}


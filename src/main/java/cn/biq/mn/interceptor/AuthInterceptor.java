package cn.biq.mn.interceptor;

import cn.biq.mn.exception.FailureMessageException;
import cn.biq.mn.exception.ItemNotFoundException;
import cn.biq.mn.utils.WebUtils;
import cn.biq.mn.book.Book;
import cn.biq.mn.book.BookRepository;
import cn.biq.mn.group.Group;
import cn.biq.mn.group.GroupRepository;
import cn.biq.mn.security.CurrentSession;
import cn.biq.mn.security.JwtUtils;
import cn.biq.mn.security.TokenBlacklist;
import cn.biq.mn.security.TokenBlacklistRepository;
import cn.biq.mn.user.User;
import cn.biq.mn.user.UserRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final CurrentSession currentSession;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final GroupRepository groupRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = WebUtils.resolveToken(request);
        if (!StringUtils.hasText(token)) {
            token = (String) request.getSession().getAttribute("accessToken");
        }
        if (!StringUtils.hasText(token)) {
            throw new FailureMessageException("user.authentication.empty");
        }
        if (tokenBlacklistRepository.existsByToken(token)) {
            throw new FailureMessageException("user.authentication.invalid");
        }
        Integer userId;
        try {
            userId = jwtUtils.getUserId(token);
        } catch (JWTVerificationException e) {
            if (!tokenBlacklistRepository.existsByToken(token)) {
                TokenBlacklist blacklist = new TokenBlacklist();
                blacklist.setToken(token);
                tokenBlacklistRepository.save(blacklist);
            }
            currentSession.setAccessToken(null);
            currentSession.setUser(null);
            currentSession.setBook(null);
            currentSession.setGroup(null);
            request.getSession().removeAttribute("accessToken");
            throw new FailureMessageException("user.authentication.invalid");
        }
        if (!token.equals(currentSession.getAccessToken())) {
            User user = userRepository.findById(userId).orElseThrow(() -> new FailureMessageException("user.authentication.invalid"));
            // 必须手动获取，不然报 org.hibernate.LazyInitializationException
            Book book = null;
            if (user.getDefaultBook() != null) {
                book = bookRepository.findById(user.getDefaultBook().getId()).orElseThrow(ItemNotFoundException::new);
            }
            Group group = groupRepository.findById(user.getDefaultGroup().getId()).orElseThrow(ItemNotFoundException::new);
            currentSession.setAccessToken(token);
            currentSession.setUser(user);
            currentSession.setBook(book);
            currentSession.setGroup(group);
        }
        return true;
    }
}

package com.chengxin.gateway.filter;

import com.chengxin.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    //定义不需要拦截的白名单路径
    private static final String[] WHITE_LIST = {"/api/auth/login","/api/auth/register","/api/chat/stream"};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        //1.判断是否在白名单
        for (String whitePath: WHITE_LIST){
            if(path.contains(whitePath)){
                return chain.filter(exchange);
            }
        }

        // 2. 获取请求头中的 token
        String token = request.getHeaders().getFirst("Authorization");
        if(token == null || token.isEmpty()){
            // ✅ 正确做法：先设置 401 状态码，再结束响应
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 💡 进阶防坑提示：通常前端传来的 Token 会带有 "Bearer " 前缀
        // 最好在这里做个容错处理，防止 JwtUtil 解析报错进入 catch 块
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 截取掉 "Bearer " 前缀
        }

//        //2.获取请求头中的token
//        String token = request.getHeaders().getFirst("Authorization");
//        if(token == null || token.isEmpty()){
//            exchange.getResponse().setComplete();
//            return exchange.getResponse().setComplete(); //拦截并返回401
//        }

        try {
            // 3. 解析 Token (这里你需要把你 auth 服务里的 JwtUtil 拷过来，或者抽离成公共模块)
            // 假设你的 JwtUtil 能解析出 userId
            // Long userId = JwtUtil.getUserIdFromToken(token);
            Claims claims = JwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());

            // 4. 核心魔法：把解析出的 userId 塞进请求头，传递给下游服务！
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .build();
            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

            return chain.filter(mutatedExchange); // 带着 userId 放行
        }catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    @Override
    public int getOrder() {
        return 0; // 过滤器执行顺序，越小越先执行
    }

}

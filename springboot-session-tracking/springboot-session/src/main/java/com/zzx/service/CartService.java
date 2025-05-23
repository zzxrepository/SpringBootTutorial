package com.zzx.service;


import com.zzx.mapper.CartItemMapper;
import com.zzx.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartService {
    @Autowired private CartItemMapper cartMapper;

    // 生成示例数据
    public List<CartItem> listByUser(Long userId) {
        return cartMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CartItem>()
                .eq("user_id", userId));
    }
}
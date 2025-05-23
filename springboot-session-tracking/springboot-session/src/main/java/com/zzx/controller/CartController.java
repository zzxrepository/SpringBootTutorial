package com.zzx.controller;


import com.zzx.context.RequestContext;
import com.zzx.model.CartItem;
import com.zzx.service.CartService;
import com.zzx.util.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired private CartService cartService;

    @GetMapping("/items")
    public ResVo<List<CartItem>> getCart() {
        Long uid = RequestContext.getUserId();
        List<CartItem> items = cartService.listByUser(uid);
        return ResVo.success(items);
    }
}
package com.github.mybatis.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.mybatis.example.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author zzq
 * @date 2021/8/17
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    
    User selectAll();
}

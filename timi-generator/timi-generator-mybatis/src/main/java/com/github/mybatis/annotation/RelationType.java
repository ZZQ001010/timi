package com.github.mybatis.annotation;

import lombok.Getter;

/**
 * 绑定关系
 *
 * @author darui.wu
 */
@Getter
public enum RelationType {
    /**
     * 单向绑定, *:1
     * 一对一 或 多对一
     */
    OneWay_0_1(false, false, false),
    /**
     * 单向绑定, *:N
     * 一对多 或 多对多
     */
    OneWay_0_N(false, false, true),
    /**
     * 双向绑定, 1:1
     * 一对一
     */
    TwoWay_1_1(true, false, false),
    /**
     * 双向绑定, 1:N
     * 一对多
     */
    TwoWay_1_N(true, false, true),
    /**
     * 双向绑定, N:1
     * 多对一
     */
    TwoWay_N_1(true, true, false),
    /**
     * 双向绑定, N:N
     * 多对多
     */
    TwoWay_N_N(true, true, true);
    /**
     * 是否双向绑定
     */
    private final boolean is2way;
    /**
     * 目标对源表：源表是否为多
     */
    private final boolean isSourceMany;
    /**
     * 源表对目标：目标是否为多
     */
    private final boolean isTargetMany;

    RelationType(boolean is2way, boolean isSourceMany, boolean isTargetMany) {
        this.is2way = is2way;
        this.isSourceMany = isSourceMany;
        this.isTargetMany = isTargetMany;
    }
}
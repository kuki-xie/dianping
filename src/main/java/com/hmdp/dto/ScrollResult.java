package com.hmdp.dto;
/**
 * 名为 ScrollResult 的 Java 类，主要用于表示分页查询的结果，
 * 特别是在处理需要滚动查询（Scroll）的场景中。该类包含了三个字段：
 */

import lombok.Data;

import java.util.List;

@Data
public class ScrollResult {
    /**
     * list：用于存储当前页的数据列表，类型为 List<?>，表示可以存放任意类型的对象列表。
     * <p>
     * minTime：表示当前数据列表中最早数据的时间戳，用于在下一次查询时作为起始点，实现数据的连续获取。
     * <p>
     * offset：表示当前数据列表的偏移量，用于在分页查询中确定数据的位置。
     */
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
/**
 * ScrollResult 类在实现基于游标的分页查询时，起到了封装查询结果和分页信息的作用，
 * 帮助开发者更方便地处理大数据量的分页查询。
 */
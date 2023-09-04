package com.kl.common.dto;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author wenyongquan
 */
@Data
public class KlPageResult<T> {
    /**
     * 总数
     */
    private Long totalCount;
    /**
     * 数据
     */
    private List<T> dataList;

    public KlPageResult() {
    }

    public KlPageResult(List<T> dataList, Long totalCount) {
        this.dataList = CollectionUtils.isEmpty(dataList) ? Collections.emptyList() : dataList;
        this.totalCount = totalCount;
    }

}

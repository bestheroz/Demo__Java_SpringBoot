package com.github.bestheroz.standard.common.dto;

import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ListResult<T> {
  private Integer page;
  private Integer pageSize;
  private Long total;
  private List<T> items;

  public static <T> ListResult<T> of(Page<T> page) {
    final ListResult<T> listResult = new ListResult<>();
    listResult.setPage(page.getNumber());
    listResult.setPageSize(page.getSize());
    listResult.setTotal(page.getTotalElements());
    listResult.setItems(page.getContent());
    return listResult;
  }
}

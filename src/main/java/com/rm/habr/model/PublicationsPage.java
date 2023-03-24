package com.rm.habr.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PublicationsPage {
    public static final int PAGE_SIZE = 10;

    /*тут хранятся не все записи, а только одна страница*/
    private List<Publication> publications;
    /*кол-во записей во всей таблице (нужно для пагинации)*/
    private int rowsCount;
}

package com.rm.habr.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Publications {
    /*тут хранятся не все записи, а только одна страница*/
    List<Publication> publications;
    /*кол-во записей во всей таблице (нужно для пагинации)*/
    int rowsCount;
}

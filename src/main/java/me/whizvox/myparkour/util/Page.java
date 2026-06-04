package me.whizvox.myparkour.util;

import java.util.List;

public record Page<T>(int page,
                      int totalItems,
                      int totalPages,
                      List<T> items) {

}

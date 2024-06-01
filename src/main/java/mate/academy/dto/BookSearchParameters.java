package mate.academy.dto;

public record BookSearchParameters(
        String[] titles,
        String[] authors,
        String[] prices
) { }

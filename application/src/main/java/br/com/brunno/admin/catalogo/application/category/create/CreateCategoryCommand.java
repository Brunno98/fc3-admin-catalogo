package br.com.brunno.admin.catalogo.application.category.create;

public record CreateCategoryCommand(
        String name,
        String description,
        boolean isActive
) {

    public static CreateCategoryCommand with(String aName, String aDescription, boolean isActive) {
        return new CreateCategoryCommand(aName, aDescription, isActive);
    }

}

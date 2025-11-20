package service;

import model.Categoria;

import java.util.List;

public interface CategoriaService {
    void guardarCategoria(Categoria categoria);
    Categoria buscarCategoriaPorId(int id);
    List<Categoria> listarCategorias();
    void actualizarCategoria(Categoria categoria);
    void eliminarCategoria(int id);
}
package com.proyecto.spring.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.proyecto.spring.model.Usuario;
import org.springframework.stereotype.Repository;
import java.util.List;


/*Esta clase sirve capturar los tipos de datos con los que se trabaja implementando una
* funcionalidad CRUD para la clase de entidad que se administra, reduciendo significativamente
* la cantidad de codigo repetitivo necesario para implementar capas de acceso a datos. Esto
* utilizando @Repository*/
@Repository
public interface UsuarioDBRepository extends CosmosRepository<Usuario, String> {
    // Solicitud para todos los metodos
    /*El @Query sirve para ejecutar un metodo del repositorio SpringData, en nuestro caso
    * el repositorio de la base de datos de CosmosDB.*/
    @Query(value = "SELECT * FROM u")
    List<Usuario> getAllUsuarios();

}

package com.proyecto.spring.controller;


import com.azure.cosmos.models.PartitionKey;
import com.proyecto.spring.repository.UsuarioDBRepository;
import com.proyecto.spring.dto.UsuarioCrudResponse;
import com.proyecto.spring.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@EnableAutoConfiguration
@RequestMapping("/api/usuarios")
@Component
public class UsuarioController {
/*@Autowired se encarga de la "inyeccion" de dependencias. En este caso, todas las
dependencias que posee nuestra clase UsuarioDBRepository, seran proporcionadas a esta
clase atravez de la variable usuarioDBRepository*/
    @Autowired
    UsuarioDBRepository usuarioDBRepository;


    Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    //Agregar nuevo usuario
    /*Esta clase recivira un objeto de tipo usuario serializandolo en la base de datos
    * esto se logra, con el @RequestBody. Una vez que */
    @PostMapping
    public ResponseEntity<UsuarioCrudResponse> createNewUsuario(@RequestBody Usuario u) {
        /*Nuestro objeto usuario(u) usara las dependencias y funcionalidades
        del la variable usuarioDBRepository usando la Inyeccion de Dependencias (IoD). Es por
        ello que podemos usar un metodo ".save()" sin que este declarado en la clase
        UsuarioDBRepository.*/
        u = usuarioDBRepository.save(u);
        //Se crea un objeto para mandar respuestas al usuario y a HTML
        UsuarioCrudResponse usuarioCrudResponse = new UsuarioCrudResponse();
        //Se manda mensaje, para condirmar que el usuario se creo
        usuarioCrudResponse.setMessage("Nuevo usuario creado correctamente con el ID: " + u.getId());
        //Se manda mensaje a HTML
        usuarioCrudResponse.setStatusCode("201: Created");
        //Se regresan los los datos para ser intermpretados y mostrados
        return new ResponseEntity<UsuarioCrudResponse>(usuarioCrudResponse, HttpStatus.CREATED);
    }

    //Actualizar un usuario existente

    /*@PutMapping Realizara una solicitud HTTP, reciviendo como parametro la caracteristica
    "Id" de nuestro objeto usuario.*/
    @PutMapping("/{id}")
    /*@PathVariable se ocupara de configurar variables dentro de los segmentos de la URL
    * Por lo que dado el parametro Id, iremos a la URL de dicho registro en nuestra BD a
    * modificar las caracateristicas del registro ingresado*/
    public ResponseEntity<UsuarioCrudResponse> updateExistingUsuario(@PathVariable String id, @RequestBody Usuario u) {
        Optional<Usuario> usuario=usuarioDBRepository.findById(id);
        usuarioDBRepository.deleteById(id, new PartitionKey(usuario.get().getNombre_usuario()));
        u.setId(id);
        usuarioDBRepository.save(u);
        UsuarioCrudResponse usuarioCrudResponse = new UsuarioCrudResponse();
        usuarioCrudResponse.setMessage("Usuario actualizado correctamente con el ID: " + u.getId());
        usuarioCrudResponse.setStatusCode("204: No content");
        //no es neceraro crear una nueva pagina sino que se sobreescriben los datos
        return new ResponseEntity<UsuarioCrudResponse>(usuarioCrudResponse, HttpStatus.NO_CONTENT);
    }


    //regresa los mdetalles de un usuario
    @GetMapping("/{id}")
    public ResponseEntity<List<Usuario>> getUsuario(@PathVariable String id) {
        //Se usa para indicar el tipo de recurso que nos devolvera
        HttpHeaders responseHeaders = new HttpHeaders();
        //Para que nos enseñe los datos en formato Json
        responseHeaders.add("ContentType", "application/json");

        List<Usuario> usuarioList = new ArrayList<>();
        //El id si coincide con algun dato de nuestra DB
        logger.info("Id is present in the GET request");

        List<Optional<Usuario>> optionaUsuarioList = Collections.singletonList(usuarioDBRepository.findById(id));
        //si el dato con el id dado son diferentes de 0 se hace lo siguiente:
        if (!(optionaUsuarioList.get(0).isEmpty())) {
            //Se va a recorrer cada campo o caracteristica de nuestro objeto y solo va a mostrar los campos
            // que no esten vacios
            //Para ver si esta b
            optionaUsuarioList.stream().forEach(u -> u.ifPresent(usuario -> usuarioList.add(usuario)));
            //Se esta enviando la lista con la informacion del usuari, el formato en que lo mostrara,
            // y el mensaje para que HTML lo identifique
            return new ResponseEntity<List<Usuario>>(usuarioList, responseHeaders, HttpStatus.OK);
        }


        return new ResponseEntity<List<Usuario>>(usuarioList, responseHeaders, HttpStatus.NOT_FOUND);

    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios(@PathVariable String id) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //Para que nos enseñe los datos en formato Json
        responseHeaders.add("ContentType", "application/json");
        List<Usuario> usuarioList = new ArrayList<>();

        logger.info("Id is not present in the GET request");
        //Se guardaran todos los usuarios, en nuestra lista usuariosList, el metodo getUsuarios de la clase
        //usuariosDBRepository
        usuarioList = usuarioDBRepository.getAllUsuarios();

        return new ResponseEntity<List<Usuario>>(usuarioList, responseHeaders, HttpStatus.OK);

    }

    //Borra un usuario con un id particular
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioCrudResponse> deleteExistingUsuario(@PathVariable String id, @RequestBody Usuario u) {
        Optional<Usuario> usuario = usuarioDBRepository.findById(id);
        usuarioDBRepository.deleteById(id, new PartitionKey(usuario.get().getNombre_usuario()));
        UsuarioCrudResponse usuarioCrudResponse = new UsuarioCrudResponse();
        usuarioCrudResponse.setMessage("Usuario eliminado correctamente con el ID: " + u.getId());
        usuarioCrudResponse.setStatusCode("204: No content");
        return new ResponseEntity<UsuarioCrudResponse>(usuarioCrudResponse, HttpStatus.NO_CONTENT);
    }
}

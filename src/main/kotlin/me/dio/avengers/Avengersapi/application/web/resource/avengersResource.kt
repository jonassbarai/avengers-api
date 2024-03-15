package me.dio.avengers.Avengersapi.application.web.resource

import jakarta.validation.Valid
import me.dio.avengers.Avengersapi.application.domain.Avenger
import me.dio.avengers.Avengersapi.application.domain.AvengerRepository
import me.dio.avengers.Avengersapi.application.web.request.AvengerRequest
import me.dio.avengers.Avengersapi.application.web.response.AvengerResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

private const val API_PATH = "/v1/api/avenger"

@RestController
@RequestMapping(API_PATH)
class avengersResource(@Autowired private val repository: AvengerRepository) {
    @GetMapping
    fun getAvengers() =
        repository.getAvengers()
            .map{ AvengerResponse.from(it)}
            .let{
                ResponseEntity.ok().body(it)
            }
    @GetMapping("/{id}")
    fun getDetail(@PathVariable("id") id: Long) =
        repository.getDetail(id)?.let {
            ResponseEntity.ok().body({AvengerResponse.from(it)})
        }?: ResponseEntity.notFound().build<Void>();
    @PostMapping
    fun createAvenger(@Valid @RequestBody request: AvengerRequest) =
        request.toAvenger().run {
           repository.create(this)
        }.let {
            ResponseEntity.created(URI("${API_PATH}/${it.id}")).body(AvengerResponse.from(it))
        }
    @PutMapping("/{id}")
    fun updateAvenger(@Valid @RequestBody request: AvengerRequest, @PathVariable("id") id: Long) =
        repository.getDetail(id)?.let {
           AvengerRequest.to(it.id,request).apply {
               repository.update(this)
           }.let {
               ResponseEntity.ok().body({AvengerResponse.from(it)})
           }
        }?: ResponseEntity.notFound().build<Void>();


    @DeleteMapping("/{id}")
    fun deleteAvenger(@PathVariable("id") id: Long) =
        repository.getDetail(id)?.let {
            ResponseEntity.accepted().build<Void>()
        }?: ResponseEntity.notFound().build<Void>();


}
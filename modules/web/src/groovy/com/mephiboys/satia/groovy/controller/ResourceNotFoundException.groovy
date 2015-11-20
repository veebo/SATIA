package com.mephiboys.satia.groovy.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {}
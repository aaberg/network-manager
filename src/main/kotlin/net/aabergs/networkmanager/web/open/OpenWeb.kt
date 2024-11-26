package net.aabergs.networkmanager.web.open

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class OpenWeb {

    @GetMapping("/")
    fun index() : String {
        return "index"
    }
}
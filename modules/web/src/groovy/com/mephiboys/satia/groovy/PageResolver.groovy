package groovy.com.mephiboys.satia.groovy

import org.springframework.ui.ModelMap

import javax.servlet.http.HttpServletRequest


class PageResolver {

    def main(ModelMap map, HttpServletRequest request){
        map["val"] = "Params are "+request.getParameterMap().toMapString();
        return "auth"
    }

}


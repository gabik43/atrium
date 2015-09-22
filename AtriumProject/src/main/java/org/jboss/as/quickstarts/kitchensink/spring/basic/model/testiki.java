package org.jboss.as.quickstarts.kitchensink.spring.basic.model;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by A.Gabdrakhmanov on 12.08.2015.
 */
@Service("testik")
public class testiki {
    @PostConstruct
    public void tesi(){
        int i = 0;
    }
}

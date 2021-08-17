package com.github.api.assembler.erm;

import com.github.meta.Database;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public final class ERMAssemblerTest {
    
    Log log =  new SystemStreamLog();
    
    @Test
    public void testAssemble() throws DocumentException {
        URL resource = ClassLoader.getSystemClassLoader().getResource("ccs.erm");
        ERMAssembler ermAssembler = new ERMAssembler();
        Database assemble = ermAssembler.assemble(new File(resource.getPath()), log);
        System.out.println(assemble);
    }

}
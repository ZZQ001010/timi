package com.github.timi.assembler;

import com.github.meta.Database;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class DMDAssemblerTest {
    
    
    @Test
    public void testAssemble() throws Exception{
        URL resource = ClassLoader.getSystemClassLoader().getResource("nccsx_cdlfddm.xml");
        File file = new File(resource.getFile());
        DMDAssembler dmdAssembler = new DMDAssembler();
        Database db = dmdAssembler.assemble(file, null);
        db.getTables().forEach(System.out::println);
    }
}

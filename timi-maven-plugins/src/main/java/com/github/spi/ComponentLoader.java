package com.github.spi;

import com.github.api.IAssembler;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author zzq
 * @date 2021/8/17
 */
public class ComponentLoader<T> {
    
    public List<T> load(Class<IAssembler> in) {
        ServiceLoader<T> ins = (ServiceLoader<T>) ServiceLoader.load(in);
        List<T> result = new ArrayList<>();
        ins.forEach(result::add);
        return result;
    }
}

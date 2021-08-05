package ${packageName};

<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
@Component
public class S${utils.dbName2ClassName(sequence)} implements SequenceOperator {
    
    @PersistenceContext
    public EntityManager em;
    
    public BigDecimal getNextValue(){
    	return JPASequenceHelper.getNextValue(em, "${sequence}");
    }
    
}
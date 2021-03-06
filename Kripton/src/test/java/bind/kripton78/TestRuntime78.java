package bind.kripton78;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import bind.AbstractBaseTest;

public class TestRuntime78 extends AbstractBaseTest {

	public BeanElement78 createBean() {
		BeanElement78 bean = new BeanElement78();
				
		bean.valueMapIntByteArray=new HashMap<>();
		byte[] a=new byte[23];
		bean.valueMapIntByteArray.put(20, new byte[0]);
		bean.valueMapIntByteArray.put(23, a);
		bean.valueMapIntByteArray.put(27, null);
		
		bean.valueListByteArray=new ArrayList<byte[]>();
		bean.valueListByteArray.add(a);
		bean.valueListByteArray.add(new byte[0]);
		bean.valueListByteArray.add(null);
		
		return bean;
	}

	@Test
	public void testRun() throws Exception {
		Assert.assertNotNull(new BeanElement78BindMap());

		BeanElement78 bean = createBean();

		check(bean);
	}


}

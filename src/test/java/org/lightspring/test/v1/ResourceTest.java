package org.lightspring.test.v1;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.core.io.ClassPathResource;
import org.lightspring.core.io.FileSystemResource;
import org.lightspring.core.io.Resource;

import java.io.InputStream;

public class ResourceTest {

	@Test
	public void testClassPathResource() throws Exception {

		Resource r = new ClassPathResource("petstore-v1.xml");

		InputStream is = null;

		try {
			is = r.getInputStream();
			// 注意：这个测试其实并不充分！！
			//真正充分的测试应该检查InputStream流，将其内容和xml比对
			Assert.assertNotNull(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}

	}

	@Test
	public void testFileSystemResource() throws Exception {

		Resource r = new FileSystemResource("E:\\some Java files\\lightspring\\src\\test\\resources\\petstore-v1.xml");

		InputStream is = null;

		try {
			is = r.getInputStream();
			// 注意：这个测试其实并不充分！！
			Assert.assertNotNull(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}

	}

}

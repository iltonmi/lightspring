package org.lightspring.test.v4;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.core.io.Resource;
import org.lightspring.core.io.support.PackageResourceLoader;

import java.io.IOException;

public class PackageResourceLoaderTest {

	@Test
	public void testGetResources() throws IOException{
		PackageResourceLoader loader = new PackageResourceLoader();
		Resource[] resources = loader.getResources("org.lightspring.dao.v4");
		Assert.assertEquals(2, resources.length);
		
	}

}

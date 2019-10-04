package org.lightspring.test.v4;

import org.junit.Assert;
import org.junit.Test;
import org.lightspring.beans.factory.annotation.AutowiredFieldElement;
import org.lightspring.beans.factory.annotation.InjectionElement;
import org.lightspring.beans.factory.annotation.InjectionMetadata;
import org.lightspring.beans.factory.support.DefaultBeanFactory;
import org.lightspring.beans.factory.xml.XmlBeanDefinitionReader;
import org.lightspring.core.io.ClassPathResource;
import org.lightspring.core.io.Resource;
import org.lightspring.dao.v4.AccountDao;
import org.lightspring.dao.v4.ItemDao;
import org.lightspring.service.v4.PetStoreService;

import java.lang.reflect.Field;
import java.util.LinkedList;

public class InjectionMetadataTest {

    @Test
    public void testInjection() throws Exception {

        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        Resource resource = new ClassPathResource("petstore-v4.xml");
        reader.loadBeanDefinitions(resource);

        Class<?> clz = PetStoreService.class;
        LinkedList<InjectionElement> elements = new LinkedList<InjectionElement>();

        {
            Field f = PetStoreService.class.getDeclaredField("accountDao");
            InjectionElement injectionElem = new AutowiredFieldElement(f, true, factory);
            elements.add(injectionElem);
        }
        {
            Field f = PetStoreService.class.getDeclaredField("itemDao");
            InjectionElement injectionElem = new AutowiredFieldElement(f, true, factory);
            elements.add(injectionElem);
        }

        InjectionMetadata metadata = new InjectionMetadata(clz, elements);

        PetStoreService petStore = new PetStoreService();

        metadata.inject(petStore);

        Assert.assertTrue(petStore.getAccountDao() instanceof AccountDao);

        Assert.assertTrue(petStore.getItemDao() instanceof ItemDao);

    }
}

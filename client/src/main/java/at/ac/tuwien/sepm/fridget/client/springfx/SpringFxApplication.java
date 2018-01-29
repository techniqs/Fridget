package at.ac.tuwien.sepm.fridget.client.springfx;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.scene.Parent;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Based on the Ticketline Example Application provided in TUWEL
 */
public abstract class SpringFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    private SpringFxmlLoader springFxmlLoader;

    protected Parent loadParent(String url) {
        return springFxmlLoader.load(url);
    }

    @Override
    public void init() throws Exception {
        super.init();
        SpringApplication springApplication = new SpringApplication(getClass());
        springApplication.addListeners((ApplicationListener<ApplicationEvent>) event -> {
            if (event instanceof ApplicationReadyEvent) {
                notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
            }
        });

        springApplication.addInitializers(ac -> {
                ac.getBeanFactory().addBeanPostProcessor(new PreloaderNotifyingBeanPostProcessor(ac));
                applicationContext = ac;
            }
        );
        ConfigurableApplicationContext ctx = springApplication.run(super.getParameters().getRaw().toArray(new String[0]));
        springFxmlLoader = ctx.getBean(SpringFxmlLoader.class);
        ctx.getAutowireCapableBeanFactory().autowireBean(this);
    }

    private String shortenBeanName(String beanName) {
        StringBuilder shortBeanNameBuilder = new StringBuilder();
        for (Iterator<String> iterator = Arrays.asList(beanName.split("\\.")).iterator(); iterator.hasNext(); ) {
            String splitName = iterator.next();
            shortBeanNameBuilder.append(iterator.hasNext() ? (splitName.charAt(0) + ".") : splitName);
        }
        return shortBeanNameBuilder.toString();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
    }

    public static class SpringProgressNotification implements Preloader.PreloaderNotification {

        private final double progress;
        private final String details;

        SpringProgressNotification(double progress, String details) {
            this.progress = progress;
            this.details = details;
        }

        public double getProgress() {
            return progress;
        }

        public String getDetails() {
            return details;
        }
    }

    private class PreloaderNotifyingBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

        private final ConfigurableApplicationContext ac;
        private double progress;
        private int loadedBeans;
        private int toLoad;

        public PreloaderNotifyingBeanPostProcessor(ConfigurableApplicationContext ac) {
            this.ac = ac;
            progress = 0;
            loadedBeans = 0;
            toLoad = 0;
        }

        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
            notifyPreloader(new SpringProgressNotification(progress, "Instantiating: " + shortenBeanName(beanClass.getName())));
            return null;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) {
            if (bean instanceof SpringFxApplication) {
                toLoad = ac.getBeanDefinitionCount() + 1;
            }
            notifyPreloader(new SpringProgressNotification(progress,
                "Instantiated: " + shortenBeanName(bean.getClass().getName())));
            return true;
        }

        @Override
        public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {
            notifyPreloader(new SpringProgressNotification(progress,
                "Setting properties: " + shortenBeanName(bean.getClass().getName())));
            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            notifyPreloader(new SpringProgressNotification(progress,
                "Initialising:" + shortenBeanName(bean.getClass().getName())));
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            loadedBeans++;
            progress = (toLoad > 0) ? ((double) loadedBeans / toLoad) : 0;
            notifyPreloader(new SpringProgressNotification(progress,
                "Initialised: " + shortenBeanName(bean.getClass().getName())));
            return bean;
        }

    }
}

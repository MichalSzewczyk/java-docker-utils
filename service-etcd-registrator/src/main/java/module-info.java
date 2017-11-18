open module container.etcd.registration {
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires etcd4j;
    exports com.szewczyk.microservices.utils.core;
}
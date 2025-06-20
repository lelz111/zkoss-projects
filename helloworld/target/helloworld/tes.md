[//]: # (<!--parent.zul-->)
<window>
    <button label="Open Child" onClick="Executions.sendRedirect('child.zul?username=aditya')" />
</window>

[//]: # (child zul)
<window>
    <label value="Username: ${param.username}" />
</window>

=======================

Map<String, Object> params = new HashMap<>();
params.put("myParam", "Hello from parent");
WIndow window = (Window) Executions.createComponents("child.zul", null, params);

[//]: # (// In view Model)
@Init
public void init(@ExecutionParam("myParam") String myParam) {
    System.out.println("Received: " + myParam);
}
======================
<include src="child.zul?foo=bar" />
<label value="Param foo: ${param.foo}" />




[//]: # (frameowrk)

SPRING.VERSION 5.2.4 RELEASE
HIBERNATE.VERSION 5.4.10


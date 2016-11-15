# My Smart Buildings - MSc Thesis 

This repository includes my MSc Thesis projects whose goal is to allow interactions between an Android smartphone and one or more Smart Buildings (buildings that make extensive use of IoT) in a secure way. This application, in fact, perfectly works in coupling with an OpenVPN or a VPN connection.

For the Server-side I used CoAPThon, a Python implementation for the CoAP protocol, which is a really good protocol for IoT devices, because it's designed for constrained networks and constrained hardware.

In my thesis project I've considered just a simple scenario, implementing, for each building:
* Open the doors;
* Set the lighting level;
* 20 Water meters;
* 10 Gas meters;
* 15 Electricity meters;
* All meters at the same time;

Responses are RESTful and can be easily managed in my android application, designed following Material Design guidelines.

##CoAPthon Custom resource:
CoAPthon custom resource, should be declared in a separated file. To import it, add these lines in **coapserver.py** file:

###coapserver.py##
```python
from myresources import LightingResource, DoorResource, WaterResource, GasResource, ElectricityResource, AllMetersResource
class CoAPServer(CoAP):
    def __init__(self, host, port, multicast=False):
        CoAP.__init__(self, (host, port), multicast)
        self.add_resource('setlighting/', LightingResource())
        self.add_resource('opendoors/', DoorResource())
        self.add_resource('watermeters/', WaterResource())
        self.add_resource('gasmeters/', GasResource())
        self.add_resource('electricitymeters/', ElectricityResource())
        self.add_resource('allmeters/', AllMetersResource())
        print "CoAP Server start on " + host + ":" + str(port)
        print self.root.dump()
```

```python
import time
import json
import collections
from random import randint
from json import JSONEncoder

from coapthon import defines
from coapthon.resources.resource import Resource

__author__ = 'Giovanni Rizzotti'
__version__ = "1.0"

def dummyvalues(metername, numbers):
    values = []
    for i in range(1, numbers+1):
    	keyname = keyname = metername + " meter 0" + str(i)
        if i >= 10:
            keyname = metername + " meter " + str(i)
    	values.append({'meterName': keyname, 'value': str(randint(1,65535))})
    return values

class LightingResource(Resource):
    def __init__(self, name="Illumination", coap_server=None):
        super(LightingResource, self).__init__(name, coap_server, visible=True,
                                            observable=True, allow_children=True)
        self.payload = "Lighting setted to 30%!"
        self.resource_type = "rt1"
        self.content_type = "text/plain"
        self.interface_type = "if1"

    def render_GET(self, request):
        return self

    def render_PUT(self, request):
        self.edit_resource(request)
        return self

    def render_POST(self, request):
        res = self.init_resource(request, LightingResource())
        return res

    def render_DELETE(self, request):
        return True

class DoorResource(Resource):
    def __init__(self, name="Door", coap_server=None):
        super(DoorResource, self).__init__(name, coap_server, visible=True,
                                            observable=True, allow_children=True)
        self.payload = "Doors opened!"
        self.resource_type = "rt1"
        self.content_type = "text/plain"
        self.interface_type = "if1"
        
    def render_GET(self, request):
    	return self

    def render_PUT(self, request):
        self.edit_resource(request)
        return self

    def render_POST(self, request):
        res = self.init_resource(request, DoorResource())
        return res

    def render_DELETE(self, request):
        return True

class WaterResource(Resource):
    def __init__(self, name="Water", coap_server=None):
        super(WaterResource, self).__init__(name, coap_server, visible=True,
                                            observable=True, allow_children=True)
        self.resource_type = "rt1"
        self.content_type = "application/json"
        self.interface_type = "if1"
        self.payload = (defines.Content_types["application/json"], json.dumps({'output': dummyvalues("Water", 20)}))
        
    def render_GET(self, request):
        return self

    def render_PUT(self, request):
        self.edit_resource(request)
        return self

    def render_POST(self, request):
        res = self.init_resource(request, WaterResource())
        return res

    def render_DELETE(self, request):
        return True

class GasResource(Resource):
    def __init__(self, name="Gas", coap_server=None):
        super(GasResource, self).__init__(name, coap_server, visible=True,
                                            observable=True, allow_children=True)
        self.resource_type = "rt1"
        self.content_type = "application/json"
        self.interface_type = "if1"
        self.payload = (defines.Content_types["application/json"], json.dumps({'output': dummyvalues("Gas", 10)}))
        
    def render_GET(self, request):
        return self

    def render_PUT(self, request):
        self.edit_resource(request)
        return self

    def render_POST(self, request):
        res = self.init_resource(request, GasResource())
        return res

    def render_DELETE(self, request):
        return True

class ElectricityResource(Resource):
    def __init__(self, name="Electricity", coap_server=None):
        super(ElectricityResource, self).__init__(name, coap_server, visible=True,
                                            observable=True, allow_children=True)
        self.resource_type = "rt1"
        self.content_type = "application/json"
        self.interface_type = "if1"
        self.payload = (defines.Content_types["application/json"], json.dumps({'output': dummyvalues("Electricity", 15)}))

    def render_GET(self, request):
        return self

    def render_PUT(self, request):
        self.edit_resource(request)
        return self

    def render_POST(self, request):
        res = self.init_resource(request, ElectricityResource())
        return res

    def render_DELETE(self, request):
        return True

class AllMetersResource(Resource):
    def __init__(self, name="AllMeters", coap_server=None):
        super(AllMetersResource, self).__init__(name, coap_server, visible=True,
                                            observable=False, allow_children=True)
        self.resource_type = "rt1"
        self.content_type = "application/json"
        self.interface_type = "if1"
        #output = json.dumps({'water': dummyvalues("Water", 20), 'gas': dummyvalues("Gas", 10), 'electricity': dummyvalues("Electricity", 15)})
        self.payload = (defines.Content_types["application/json"], json.dumps({'water': dummyvalues("Water", 20), 'gas': dummyvalues("Gas", 10), 'electricity': dummyvalues("Electricity", 15)}))
        

    def render_GET(self, request):
        return self

    def render_PUT(self, request):
        self.edit_resource(request)
        return self

    def render_POST(self, request):
        res = self.init_resource(request, AllMetersResource())
        return res

    def render_DELETE(self, request):
        return True
```

##CoAPThon execution
###Windows batch file
```
@ECHO OFF
setlocal enabledelayedexpansion
set COAP_PORT=5683
set /P INPUT=Number of servers: 
start python IPv4Server.py -p / -hp PROXY_PORT -ip 192.168.1.2 -cp !port!
FOR /L %%I IN (1, 1, %INPUT%) DO ( 
		start python coapserver.py -i 192.168.1.2 -p !port!
 		set /a port+=1
)
```
In this batch script, you have to specify:
* CoAP Server Port (eg **5683**);
* Proxy Server Port

###OSX script file
```
#!/bin/sh
port=5683
echo Enter the number of servers
read N
python IPv4Server.py -p / -hp 8080 -ip 192.168.1.3 -cp !port! &
for (( i=0; i<N; i++ ))
do
	python coapserver.py -i 192.168.1.3 -p $((port + i)) -m "" &
done
```

![01main](https://cloud.githubusercontent.com/assets/11563183/20307167/8f07e020-ab3e-11e6-8b8c-b839f9411f61.jpg) ![02drawer](https://cloud.githubusercontent.com/assets/11563183/20307164/8f05643a-ab3e-11e6-9c87-e0eb92b73d2c.jpg)
![03configuration](https://cloud.githubusercontent.com/assets/11563183/20307165/8f061bbe-ab3e-11e6-9434-b01865c20f80.jpg) ![04operations](https://cloud.githubusercontent.com/assets/11563183/20307166/8f07e12e-ab3e-11e6-9839-4035c9544f32.jpg)
![06light](https://cloud.githubusercontent.com/assets/11563183/20307169/8f0b519c-ab3e-11e6-9d02-7477baef6401.jpg) ![07doors](https://cloud.githubusercontent.com/assets/11563183/20307168/8f08f6b8-ab3e-11e6-85eb-466999386dc3.jpg)
![08water](https://cloud.githubusercontent.com/assets/11563183/20307171/8f1f52e6-ab3e-11e6-9c28-052c45047f2e.jpg) ![09gas](https://cloud.githubusercontent.com/assets/11563183/20307173/8f20d918-ab3e-11e6-8a3d-94c37a5cd1e1.jpg)
![10electricity](https://cloud.githubusercontent.com/assets/11563183/20307172/8f20bc44-ab3e-11e6-8bea-c87354b16447.jpg) ![11allmeters](https://cloud.githubusercontent.com/assets/11563183/20307174/8f22022a-ab3e-11e6-956b-9e27e1bea61d.jpg)
![12allbuildings](https://cloud.githubusercontent.com/assets/11563183/20307175/8f27e0aa-ab3e-11e6-91c0-1eeb060d4a13.jpg)

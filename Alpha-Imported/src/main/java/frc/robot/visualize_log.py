import matplotlib.pyplot as plt
import json

with open('s=0.6, t=0.15, e=3.0.json') as file:
	data = json.load(file)

	time = []
	angle = []
	for e in data:
		time.append(float(e['timestamp']))
		angle.append(float(e['line'][:-1]))




plt.plot(time, angle)
plt.show()
import matplotlib.pyplot as plt
import json

with open('log2.json') as file:
	data = json.load(file)

	time = []
	angle = []
	for e in data:
		time.append(float(e['timestamp']))
		angle.append(float(e['line'][:-1]))




plt.plot(time, angle)
plt.show()
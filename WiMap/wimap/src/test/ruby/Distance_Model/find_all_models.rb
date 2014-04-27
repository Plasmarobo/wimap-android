require './distance_model.rb'
require 'simpleoutput'
require 'simpleplot'
require 'simplechartkick'

graph = SimpleOutput::SimpleOutputEngine.new
plot = SimplePlot.new("_distance")
graph.addPlugin(plot)

model = DistanceModel.new
csv_files = Dir["./*.csv"]
csv_files.each do |file|
	power_map = model.parse_powermap_csv(file)
	coeff = model.find_ptx(power_map)
	
	predictions = {}
	min = 100
	power_map.each_pair do |(key, value)|
		if !predictions.has_key?(key)
			predictions[key] = []
		end
		if value.size < min
			min = value.size
		end
		value.each do |dBm|
			predictions[key] << model.distance(dBm.to_f,2400.0,coeff)
		end
	end
	i = 0
	x = []
	y = []
	30.times do
		y << []
		x << i
		i += 1
	end
	i = 0
	min.times do
		y = []
		j = 0
		while j < 30
			y << predictions[j][i] 
			j += 1
		end 
		graph.appendXY(x,y,"DistanceModel", {'series' => "Prediction #{i}", 'xsize' => 3000, 'ysize' => 1000})
		i += 1
	end
end
graph.save()
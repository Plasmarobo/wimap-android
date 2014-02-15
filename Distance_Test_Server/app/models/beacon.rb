class Beacon < ActiveRecord::Base
	has_many :distance_samples
end

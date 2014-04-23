class Beacon < ActiveRecord::Base
	has_many :distance_samples, dependent: :destroy
end

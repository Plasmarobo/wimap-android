class DistanceSamplesController < ApplicationController
  skip_before_filter :verify_authenticity_token, :only => :create
  before_action :set_distance_sample, only: [:show, :edit, :update, :destroy]

  # GET /distance_samples
  # GET /distance_samples.json
  def index
    @distance_samples = DistanceSample.all
  end

  # GET /distance_samples/1
  # GET /distance_samples/1.json
  def show
  end

  # GET /distance_samples/new
  def new
    @distance_sample = DistanceSample.new
  end

  # GET /distance_samples/1/edit
  def edit
  end

  # POST /distance_samples
  # POST /distance_samples.json
  def create
    distance_sample = JSON.parse(params["distance_sample"]);
    @distance_sample = DistanceSample.new(distance_sample)

    respond_to do |format|
      if @distance_sample.save
        format.html { redirect_to @distance_sample, notice: 'Distance sample was successfully created.' }
        format.json { render action: 'show', status: :created, location: @distance_sample }
      else
        format.html { render action: 'new' }
        format.json { render json: @distance_sample.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /distance_samples/1
  # PATCH/PUT /distance_samples/1.json
  def update
    respond_to do |format|
      if @distance_sample.update(distance_sample_params)
        format.html { redirect_to @distance_sample, notice: 'Distance sample was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: 'edit' }
        format.json { render json: @distance_sample.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /distance_samples/1
  # DELETE /distance_samples/1.json
  def destroy
    @distance_sample.destroy
    respond_to do |format|
      format.html { redirect_to distance_samples_url }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_distance_sample
      @distance_sample = DistanceSample.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def distance_sample_params
      params.require(:distance_sample).permit(:integer, :integer, :integer)
    end
end
